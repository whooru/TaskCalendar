package com.example.taskcalendar.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskcalendar.R
import com.example.taskcalendar.objects.CMonth
import com.example.taskcalendar.objects.Friend
import com.example.taskcalendar.objects.User
import com.example.taskcalendar.objects.Year
import com.example.taskcalendar.veiwsstate.MonthListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.month_main.*
import kotlinx.coroutines.*
import org.threeten.bp.LocalDateTime


class CalendarActivity : AppCompatActivity() {

    private var adapter: MonthListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month_main)
        val currentDate = LocalDateTime.now()
        var currentYear = currentDate.year

        val user = intent.getSerializableExtra("user") as User
        val calendarName = intent.getStringExtra("calendar")!!
        var friend: Friend? = null
        if (intent.getSerializableExtra("friend") != null) {
            friend = intent.getSerializableExtra("friend")!! as Friend
        }
        //sort months list from user's calendar
        val sortedMonthList =
            generateData(user.calendarsList[calendarName]!!.yearsList[currentYear.toString()]!!.monthsList.values)
        val recyclerView = recycler_view
        adapter = MonthListAdapter(sortedMonthList, user, calendarName, currentDate)
        val layoutManager = LinearLayoutManager(this)
        setOptionsForRecycler(recyclerView, layoutManager, adapter!!)


        //подгрузка следующего года, когда список подходит к концу
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var previousTotal = 0
            var loading = true
            val visibleThreshold = 6
            var firstVisibleItem = 0
            var visibleItemCount = 0
            var totalItemCount = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                visibleItemCount = recyclerView.childCount
                totalItemCount = layoutManager.itemCount
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val initialSize = adapter!!.monthsList.size
                        currentYear++
                        val list = mutableListOf<CMonth>()
                        var nextYear =
                            user.calendarsList[calendarName]!!.yearsList[currentYear.toString()]
                        if (nextYear != null) {
                            list.addAll(nextYear.monthsList.values)
                        } else {
                            val path = FirebaseFirestore.getInstance().collection("users")
                                .document(user.email)
                                .collection("calendars").document(calendarName)
                            user.calendarsList[calendarName]!!.addYear(
                                path,
                                Year(currentYear)
                            )
                            nextYear =
                                user.calendarsList[calendarName]!!.yearsList[currentYear.toString()]
                            list.addAll(nextYear!!.monthsList.values)
                        }
                        val updatedSize = updateDataList(list)
                        updateAdapter(recyclerView, initialSize, updatedSize)
                    }
                    loading = true
                }
            }
        })
    }

    private fun setOptionsForRecycler(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        adapter: MonthListAdapter
    ) {
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun generateData(list: MutableCollection<CMonth>): MutableList<CMonth> {
        val sortedMonthList: MutableList<CMonth> = mutableListOf()
        sortedMonthList.addAll(list)
        for (month in list) {
            sortedMonthList[month.numberOfMonth!! - 1] = month
        }
        return sortedMonthList
    }


    private fun updateDataList(list: MutableList<CMonth>): Int {
        adapter!!.monthsList.addAll(list)
        return adapter!!.monthsList.size
    }

    fun updateAdapter(recyclerView: RecyclerView, initialSize: Int, updatedSize: Int) {
        recyclerView.post {
            adapter!!.notifyItemRangeInserted(
                initialSize,
                updatedSize
            )
        }
    }
}
