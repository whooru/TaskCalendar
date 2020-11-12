package com.example.taskcalendar.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_registration.*


class RegActivity : AppCompatActivity() {

    var TAG = "TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_registration)
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth

        confirmRegistration.setOnClickListener {
            if (enterName.text.toString().length > 3
                && enterLogin.text.toString().length > 4
                && enterPassword.text.toString().length > 6
                && reenterPassword.text.toString() == enterPassword.text.toString()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(enterEmail.text.toString()).matches()
            ) {
                val user = User(
                    enterName.text.toString(),
                    enterLogin.text.toString(),
                    enterEmail.text.toString()
                )
                auth.createUserWithEmailAndPassword(
                    enterEmail.text.toString(),
                    enterPassword.text.toString()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            db.collection("users").document(user.email).set(user)

//                            val user = auth.currentUser
//                            user?.displayName
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        haveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}