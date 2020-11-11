package com.example.taskcalendar.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.create_calendar_fragment.*


class CreateCalendar(activity: Activity) : DialogFragment() {

    private var mEditText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Выберите котов")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(com.example.taskcalendar.R.layout.create_calendar_fragment, container)
        mEditText = txt_calendar_name
        dialog!!.setTitle("Hello")
        return super.onCreateView(inflater, container, savedInstanceState)
    }


}