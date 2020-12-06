package com.example.taskcalendar.veiwsstate


import com.example.taskcalendar.objects.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import java.lang.Exception
class UserState(private val db: FirebaseFirestore) {

    suspend fun downloadUser(
        currentUser: String
    ): User? {
        val docRef = db.collection("users").document(currentUser)
        var user: User? = null
        try {
            val d = docRef.get().await()
            user = d.toObject(User::class.java)
        } catch (e: Exception) {
            return null
        }
        return user
    }

    suspend fun downloadUsersLogin(): MutableMap<String, String> {
        val path = db.collection("usersLogin")
        val loginList: MutableMap<String, String> = mutableMapOf()
        try {
            val list = path.get(Source.SERVER).await()
            for (document in list.documents) {
                loginList[document.id] = document.data?.get("email").toString()
            }
        } catch (e: Exception) {
        }
        return loginList
    }

    suspend fun downloadCalendars(email: String) : MutableList<String> {
        val path = db.collection("users").document(email).collection("calendars")
        val calendarList = mutableListOf<String>()
        val documents = path.whereEqualTo("public", true).get().await()
        for (document in documents){
            calendarList.add(document.id)
        }
        return calendarList
    }
}