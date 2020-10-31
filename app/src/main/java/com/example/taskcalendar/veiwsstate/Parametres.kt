package com.example.taskcalendar.veiwsstate

import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow

class Parametres {
    fun getDayParams(): TableRow.LayoutParams {
        return TableRow.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.2f
        )
    }

    fun getTableParams(): TableLayout.LayoutParams {
        return TableLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }
}