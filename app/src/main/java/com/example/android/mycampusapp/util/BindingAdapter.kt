package com.example.android.mycampusapp.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.timetable.days.monday.MondayAdapter

@BindingAdapter("mondaySubject")
fun TextView.bindSubject(item:MondayClass?){
    item?.let {
        text = item.subject
    }
}
@BindingAdapter("mondayTime")
fun TextView.bindTime(item:MondayClass?){
    item?.let {
        text = item.time
    }
}
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,data:List<MondayClass>?){
    val adapter:MondayAdapter = recyclerView.adapter as MondayAdapter
    adapter.submitList(data)
}