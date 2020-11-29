package com.example.taskcalendar.fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskcalendar.R
import com.example.taskcalendar.activities.CalendarActivity
import com.example.taskcalendar.objects.Friend
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.veiwsstate.MainViewState
import com.example.taskcalendar.veiwsstate.Parametres
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_friend_popup.*
import kotlinx.android.synthetic.main.create_calendar_fragment.*
import kotlinx.android.synthetic.main.create_calendar_fragment.btn_cancel
import kotlinx.android.synthetic.main.freinds_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.util.*

class FriendsPanelFragment() : Fragment() {
    var usersFriendsList: MutableMap<String, Friend> = mutableMapOf()
    var loginList: MutableMap<String, Any?> = mutableMapOf()
    var user: User? = null
    var snapshot: ListenerRegistration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = FirebaseFirestore.getInstance()
        val currentUser = Firebase.auth.currentUser!!.email
        val docRef = db.collection("users").document(currentUser.toString())
        runBlocking {
            GlobalScope.launch {
                Tasks.await(docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val userdb = document.toObject(User::class.java)!!
                            user = userdb
                        } else {
                            Log.d(ContentValues.TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(ContentValues.TAG, "get failed with ", exception)
                    })
            }.join()
        }
        val loginRef = db.collection("usersLogin")
        runBlocking {
            GlobalScope.launch {
                Tasks.await(loginRef.get().addOnSuccessListener {
                    documents ->
                    if (documents !=null){
                        for (document in documents){
                            loginList[document.id] = document.data["email"]
                        }
                    }
                })
            }
        }
        return inflater.inflate(R.layout.freinds_fragment, container, false);

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        add_friend.setOnClickListener {
            addFriend(this.context!!)
        }
        val userAuth = Firebase.auth.currentUser!!.email
        val db = FirebaseFirestore.getInstance()
        val pathFriends = db.collection("users").document(userAuth.toString()).collection("friends")
        val pathForFriends = db.collection("usersLogin")

        snapshot = pathFriends.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d("FRIEND", "New Friend: ${dc.document.data}")
                        val friendBtn = Button(activity)
                        friendBtn.text = dc.document.id
                        friendBtn.layoutParams = Parametres().getDayParams()
                        friendBtn.width = 150
                        friendBtn.height = 150
                        friendBtn.setOnClickListener {
                            //TODO

                        }
                        friends_list.addView(friendBtn)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d("FRIEND", "Modified friend: ${dc.document.data}")
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d("FRIEND", "Removed friend: ${dc.document.data}")

                    }
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snapshot!!.remove()
    }


    private fun addFriend(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.add_friend_popup)
        dialog.show()
        dialog.btn_add_friend.setOnClickListener {
            val friendLogin = dialog.txt_friend_login.text.toString()
            if (user != null) {
                if (loginList.containsKey(friendLogin)) {
                    user?.addFriend(friendLogin)
                    dialog.cancel()
                }else{
                    Toast.makeText(
                        context, "User does not exist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        dialog.btn_cancel.setOnClickListener {
            dialog.cancel()
        }
    }
}

