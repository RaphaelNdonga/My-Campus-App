package com.example.android.mycampusapp.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.SundayClass
import com.example.android.mycampusapp.data.TuesdayClass
import com.example.android.mycampusapp.display.days.monday.MondayAdapter
import com.example.android.mycampusapp.display.days.sunday.SundayAdapter
import com.example.android.mycampusapp.display.days.tuesday.TuesdayAdapter

@BindingAdapter("mondaySubject")
fun TextView.bindMondaySubject(item:MondayClass?){
    item?.let {
        text = item.subject
    }
}
@BindingAdapter("mondayTime")
fun TextView.bindMondayTime(item:MondayClass?){
    item?.let {
        text = item.time
    }
}
@BindingAdapter("mondayListData")
fun bindMondayRecyclerView(recyclerView: RecyclerView,data:List<MondayClass>?){
    val adapter:MondayAdapter = recyclerView.adapter as MondayAdapter
    adapter.submitList(data)
}

@BindingAdapter("tuesdaySubject")
fun TextView.bindTuesdaySubject(item: TuesdayClass?){
    item?.let{
        text = item.subject
    }
}

@BindingAdapter("tuesdayTime")
fun TextView.bindTuesdayTime(item: TuesdayClass?){
    item?.let {
        text = item.time
    }
}

@BindingAdapter("tuesdayListData")
fun bindTuesdayRecyclerView(recyclerView:RecyclerView, data: List<TuesdayClass>?){
    val adapter = recyclerView.adapter as TuesdayAdapter
    adapter.submitList(data)
}

@BindingAdapter("sundaySubject")
fun TextView.bindSundaySubject(item:SundayClass?){
    text = item?.subject
}
@BindingAdapter("sundayTime")
fun TextView.bindSundayTime(item:SundayClass?){
    text = item?.time
}
@BindingAdapter("sundayListData")
fun bindRecyclerView(recyclerView: RecyclerView,data: List<SundayClass>?){
    val adapter = recyclerView.adapter as SundayAdapter
    adapter.submitList(data)
}