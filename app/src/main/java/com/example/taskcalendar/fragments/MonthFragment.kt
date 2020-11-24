package com.example.taskcalendar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.objects.Year
import com.example.taskcalendar.veiwsstate.MonthListAdapter
import com.example.taskcalendar.veiwsstate.Parametres
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_calendar_list.*
//import kotlinx.android.synthetic.main.activity_calendar_list.ScrollMonth
import kotlinx.android.synthetic.main.activity_calendar_list.view.*
import kotlinx.android.synthetic.main.fragment_month.*
import kotlinx.android.synthetic.main.month_main.*
import org.threeten.bp.LocalDateTime
import kotlin.concurrent.thread


class MonthFragment() : Fragment() {
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.month_main, container, false);
    }

}

