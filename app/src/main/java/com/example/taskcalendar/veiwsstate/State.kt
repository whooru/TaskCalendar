package com.example.taskcalendar.veiwsstate

import android.app.Activity
import com.example.taskcalendar.objects.User

interface State {
    fun updateState(activity: Activity, user: User)
}