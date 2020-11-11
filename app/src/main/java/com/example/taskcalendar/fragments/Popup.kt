package com.example.taskcalendar.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.os.Bundle

interface Popup : DialogInterface{
    fun onCreateDialog(savedInstanceState: Bundle, activity: Activity) : Dialog {
        val builder = AlertDialog.Builder(activity)
        return builder.setTitle("Диалоговое окно").setMessage("Для закрытия окна нажмите ОК")
            .create()
    }
    }
