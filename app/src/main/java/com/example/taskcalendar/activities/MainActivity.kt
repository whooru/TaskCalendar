package com.example.taskcalendar.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.R
import com.example.taskcalendar.veiwsstate.UserState
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    lateinit var user: User
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        val userState = UserState()
        userState.downloadUser(FirebaseFirestore.getInstance(), Firebase.auth.currentUser, this@MainActivity )
//        fun downloadUser() = runBlocking {GlobalScope.launch { User().downloadUser(db, currentUser, this@MainActivity) }.join()  }
////        downloadUser()
//        user = User()
//        fun downloadUser() = runBlocking {GlobalScope.launch { user.downloadUser(db, currentUser)}.join()}
//        downloadUser()
//        GlobalScope.async { UserState().downloadUser(db, currentUser) }
//        if (UserState().user.name.length> 2) {
//            val textView = TextView(this)
//            textView.text = "Hello ${user.name}! \n" +
//                    "You have ${user.calendarsList.size} calendars \n" +
//                    "Change it"
//            clayout.addView(textView)
//            for (calendar in user.calendarsList.keys) {
//                val button = Button(this)
//                button.text = calendar
//                clayout.addView(button)
//            }
//        }

//    private fun downloadUser(db: FirebaseFirestore, currentUser: FirebaseUser?): User? {
//        var user: User? = null
//        val docRef = db.collection("users").document(currentUser?.email.toString())
//        docRef.get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    user = document.toObject(User::class.java)
//                    Log.d("INFO", "DocumentSnapshot data: ${document.data}")
//                } else {
//                    Log.d("INFO", "No such document")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("INFO", "get failed with ", exception)
//            }
//        return user
//    }
    }
}





