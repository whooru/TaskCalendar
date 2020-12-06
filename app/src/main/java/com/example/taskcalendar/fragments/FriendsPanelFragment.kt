package com.example.taskcalendar.fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.Friend
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.veiwsstate.Parametres
import com.example.taskcalendar.veiwsstate.UserState
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
import kotlinx.android.synthetic.main.friend_calendars_list_popup.*
import kotlinx.coroutines.*
import java.util.*

class FriendsPanelFragment() : Fragment() {
    var loginList: MutableMap<String, String> = mutableMapOf()
    var user: User? = null
    var snapshot: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    private val userState = UserState(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val currentUser = Firebase.auth.currentUser!!.email.toString()
        CoroutineScope(Dispatchers.Main).launch {
            user = userState.downloadUser(currentUser)
            loginList = userState.downloadUsersLogin()
        }
        return inflater.inflate(R.layout.freinds_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        add_friend.setOnClickListener {
            addFriend(context!!)
        }
        val userAuth = Firebase.auth.currentUser!!.email
        val pathFriends = db.collection("users").document(userAuth.toString()).collection("friends")
        snapshot = pathFriends.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "listen:error", e)
                return@addSnapshotListener
            }
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.d("FRIEND", "New Friend: ${dc.document.id}")
                        val friendBtn = friendBtn(dc.document.id)
                        friends_list.addView(friendBtn)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d("FRIEND", "Modified friend: ${dc.document.id}")
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d("FRIEND", "Removed friend: ${dc.document.id}")
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

    private suspend fun showFriendCalendars(friend: Friend): Dialog {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.friend_calendars_list_popup)
        dialog.friendName.text = friend.login
        val calendarList: MutableList<String> = userState.downloadCalendars(friend.email)
        println(calendarList)
        if (calendarList.isNotEmpty()) {
            for (calendarName in calendarList) {
                val calendar = Button(context)
                calendar.text = calendarName
                calendar.setOnClickListener {
                    //TODO

                    //snapshot!!.remove()
                }
                dialog.friend_calendars_list.addView(calendar)
            }
        }
        return dialog
    }

    private fun addFriend(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.add_friend_popup)
        dialog.show()
        dialog.btn_add_friend.setOnClickListener {
            val friendLogin = dialog.txt_friend_login.text.toString()
            if (user != null) {
                if (loginList.containsKey(friendLogin)) {
                    user?.addFriend(friendLogin, loginList[friendLogin].toString())
                    dialog.cancel()
                } else {
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

    private fun friendBtn(friendName: String): Button {
        val friendBtn = Button(activity)
        friendBtn.text = friendName
        friendBtn.layoutParams = Parametres().getDayParams()
        friendBtn.width = 150
        friendBtn.height = 150
        friendBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                showFriendCalendars(user!!.friendsList[friendName]!!).show()
            }
        }
        return friendBtn
    }
}

