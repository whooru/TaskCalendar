package com.example.taskcalendar.objects


import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable


data class User(
    var name: String = "",
    var login: String = "",
    var email: String = "",
    var calendarsMap: MutableMap<String, String> = mutableMapOf(),
    var calendarsList: MutableMap<String, CCalendar> = mutableMapOf(),
    var friendsList: MutableMap<String, Friend> = mutableMapOf()
) : Serializable {

    fun addCalendar(calendarName: String, access: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val calendar = CCalendar(calendarName, login, access)
        val path =
            db.collection("users").document(email).collection("calendars").document(calendar.name)
        calendar.makeCalendar(path)
        calendarsList[calendar.name] = calendar
        db.collection("users").document(email).set(this)
        path.set(calendar)
        updateDb()
    }

    fun addFriend(friendsLogin: String){
        val db = FirebaseFirestore.getInstance()
        val friend = Friend(friendsLogin)
        friendsList[friendsLogin] = friend
        val path = db.collection("users").document(email).collection("friends").document(friendsLogin)
        path.set(friend)
        updateDb()
    }

    fun removeFriend(friendsEmail: String){
        val db = FirebaseFirestore.getInstance()
        val path = db.collection("users").document(email).collection("friends").document(friendsEmail)

    }

    fun updateDb(){
        val db = FirebaseFirestore.getInstance()
        val path = db.collection("users").document(email)
        path.set(this)
    }
}
