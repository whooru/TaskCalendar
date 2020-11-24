package com.example.taskcalendar.veiwsstate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.User
import org.threeten.bp.LocalDateTime

class MonthListAdapter(
    val monthsList: MutableList<CMonth>,
    val user: User,
    private val calendarName: String,
    private val date: LocalDateTime
) :
    RecyclerView.Adapter<MonthViewHolder>() {
    var position: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MonthViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        this.position = position
        val month = monthsList[position]
        holder.bind(month, user, calendarName, date)
    }


    override fun getItemCount() = monthsList.size


}

